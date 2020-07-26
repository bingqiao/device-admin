package com.bqiao.service.impl;

import com.bqiao.domain.Device;
import com.bqiao.event.LifeCycleEventPublisher;
import com.bqiao.repo.jpa.CustomFieldRepo;
import com.bqiao.repo.jpa.DeviceRepo;
import com.bqiao.repo.RepositoryService;
import com.bqiao.service.DeviceDocService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class DeviceServiceImplTests {

    private static final String TEST_PROFILE = "test-profile";
    private static final String TEST_DEVICE_ID = "test-device-id";

    @MockBean
    private DeviceRepo deviceRepo;

    @MockBean
    LifeCycleEventPublisher publisher;

    @MockBean
    private CustomFieldRepo customFieldRepo;

    @MockBean
    private RepositoryService repoService;

    private DeviceServiceImpl underTest;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void beforeEach() {
        underTest = new DeviceServiceImpl(publisher, repoService);
        given(repoService.getDeviceRepo()).willReturn(deviceRepo);
        given(repoService.getCustomFieldRepo()).willReturn(customFieldRepo);
    }

    @Test
    public void givenOneDevice_whenGetDevices_thenReturnOneDevice() {
        List<Device> devices = new ArrayList<>();
        Device device = new Device();
        devices.add(device);
        given(deviceRepo.findAll()).willReturn(devices);
        List<Device> retrieved = underTest.getDevices(Optional.empty());

        then(deviceRepo).should().findAll();
        then(deviceRepo).shouldHaveNoMoreInteractions();
        assertEquals(1, retrieved.size());
    }

    @Test
    public void givenNewDevice_whenCreateDevice_thenReturnCreatedDevice() {
        Device toCreate = new Device();

        toCreate.setProfile(TEST_PROFILE);

        given(deviceRepo.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        Device created = underTest.createDevice(toCreate);
        then(deviceRepo).should().save(created);
    }

    @Test
    public void givenNullDevice_whenCreateDevice_thenThrowException() {
        thrown.expect(ResponseStatusException.class);
        thrown.expectMessage("400 BAD_REQUEST \"Invalid object\"");
        underTest.createDevice(null);
    }

    @Test
    public void givenDeviceWithInvalidEmail_whenCreateDevice_thenThrowException() {
        thrown.expect(ResponseStatusException.class);
        thrown.expectMessage("400 BAD_REQUEST \"Invalid object\"");
        Device device = new Device();
        underTest.createDevice(device);
    }

    @Test
    public void givenDeviceAbsent_whenDeleteDevice_thenThrowException() {
        given(deviceRepo.findById(TEST_DEVICE_ID)).willReturn(Optional.empty());
        thrown.expect(ResponseStatusException.class);
        thrown.expectMessage("404 NOT_FOUND \"Object not found\"");
        underTest.deleteDevice(TEST_DEVICE_ID);
    }

    @Test
    public void givenDevice_whenDeleteDevice_thenExpectSuccess() {
        Optional<Device> device = Optional.of(new Device());
        given(deviceRepo.findById(TEST_DEVICE_ID)).willReturn(device);
        underTest.deleteDevice(TEST_DEVICE_ID);
        then(deviceRepo).should().findById(TEST_DEVICE_ID);
        then(deviceRepo).should().delete(device.get());
    }
}
