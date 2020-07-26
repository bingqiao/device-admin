package com.bqiao.service.impl;

import com.bqiao.domain.CustomField;
import com.bqiao.domain.Device;
import com.bqiao.event.LifeCycleEventPublisher;
import com.bqiao.repo.RepositoryService;
import com.bqiao.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

import static com.bqiao.event.LifeCycleEventAction.*;
import static java.util.stream.Collectors.*;

@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

	private final RepositoryService repoService;
	private final LifeCycleEventPublisher publisher;

	public DeviceServiceImpl(
			LifeCycleEventPublisher publisher,
			RepositoryService repoService) {
		this.publisher = publisher;
		this.repoService = repoService;
	}

	@Override
	public List<Device> getDevices(Optional<List<String>> ids) {
		List<Device> devices;
		if (ids.isEmpty()) {
			devices = this.repoService.getDeviceRepo().findAll();
			devices.forEach(device -> {
				device.setCustomFields(findCustomFields(device.getId()));
			});
		} else {
			devices = this.getDevices(ids.get());
		}
		return devices;
	}

	@Override
	public Device getDevice(String id) {
		Device found = findDevice(id);
		found.setCustomFields(findCustomFields(id));
		return found;
	}

	@Override
	@Transactional
	public void deleteDevice(String id) {
		Device found = findDevice(id);
		this.repoService.getCustomFieldRepo().deleteByDeviceId(id);
		this.repoService.getDeviceRepo().delete(found);
		publisher.publishIds(List.of(id), DELETED, true);
	}

	@Override
	public Device updateDevice(String id, Device toUpdate) {
		Device found = findDevice(id);
		found.copy(toUpdate);
		Map<Integer, String> fieldsToUpdate = toUpdate.getCustomFields();
		if (fieldsToUpdate == null) {
			fieldsToUpdate = Collections.emptyMap();
		}
		Map<Integer, String> fieldsUpdated = new HashMap<>();
		List<CustomField> fields = this.repoService.getCustomFieldRepo().findByDeviceId(id);
		CustomField[] ordered = new CustomField[CustomField.NUMBER_OF_CUSTOM_FIELDS];
		fields.forEach(field -> {
			ordered[field.getFieldOrder()] = field;
		});

		for (int i = 0; i < CustomField.NUMBER_OF_CUSTOM_FIELDS; i++) {
			if (ordered[i] == null) {
				String fieldValue = fieldsToUpdate.get(i);
				if (fieldValue != null) {
					CustomField fieldToCreate = new CustomField(fieldValue);
					fieldToCreate.setDeviceId(id);
					fieldToCreate.setFieldOrder(i);
					CustomField created = this.repoService.getCustomFieldRepo().save(fieldToCreate);
					fieldsUpdated.put(i, created.getValue());
				}
			} else {
				String fieldValue = fieldsToUpdate.get(i);
				if (fieldValue == null) {
					this.repoService.getCustomFieldRepo().delete(ordered[i]);
				} else {
					CustomField fieldToUpdate = new CustomField(fieldValue);
					fieldToUpdate.setDeviceId(id);
					fieldToUpdate.setFieldOrder(i);
					CustomField updated = this.repoService.getCustomFieldRepo().save(fieldToUpdate);
					fieldsUpdated.put(i, updated.getValue());
				}
			}
		}

		found.setCustomFields(fieldsUpdated);

		this.publisher.publish(List.of(found), UPDATED, true);

		return found;
	}

	@Override
	public Device createDevice(Device toCreate) {
		if (toCreate == null || StringUtils.isEmpty(toCreate.getProfile())) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Invalid object");
		}
		Device created = this.repoService.getDeviceRepo().save(toCreate);
		Map<Integer, String> fields = toCreate.getCustomFields();
		if (!CollectionUtils.isEmpty(fields)) {
			Map<Integer, String> fieldsCreated = fields.keySet().stream().filter(key -> key < CustomField.NUMBER_OF_CUSTOM_FIELDS)
					.map(key -> {
						CustomField field = new CustomField(fields.get(key));
						field.setDeviceId(created.getId());
						field.setFieldOrder(key);
						return this.repoService.getCustomFieldRepo().save(field);
					}).collect(toMap(CustomField::getFieldOrder, CustomField::getValue));
			created.setCustomFields(fieldsCreated);
		}
		publisher.publish(List.of(created), CREATED, true);
		return created;
	}

	@Override
	public List<Device> getDevices(List<String> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		List<Device> devices = repoService.getDeviceRepo().findAllById(ids);
		List<CustomField> fields = repoService.getCustomFieldRepo().findByDeviceIdIn(ids);
		Map<String, Map<Integer, String>> deviceFields = fields.stream()
				.collect(groupingBy(CustomField::getDeviceId, toMap(CustomField::getFieldOrder, CustomField::getValue)));
		return devices.stream().map(device -> {
			if (deviceFields.containsKey(device.getId())) {
				device.setCustomFields(deviceFields.get(device.getId()));
			}
			return device;
		}).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void deleteDevices(Optional<List<String>> ids) {
		if (ids.isEmpty()) {
			repoService.getCustomFieldRepo().deleteAll();
			repoService.getDeviceRepo().deleteAll();
		} else {
			repoService.getCustomFieldRepo().deleteByDeviceIdIn(ids.get());
			repoService.getDeviceRepo().deleteByIdIn(ids.get());
			publisher.publishIds(ids.get(), DELETED, true);
		}
	}

	private Device findDevice(String id) {
		Optional<Device> found = this.repoService.getDeviceRepo().findById(id);
		if (found.isEmpty()) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Object not found");
		}
		return found.get();
	}

	private Map<Integer, String> findCustomFields(String id) {
		return this.repoService.getCustomFieldRepo().findByDeviceId(id)
				.stream().collect(toMap(CustomField::getFieldOrder, CustomField::getValue));
	}
}
