package com.bqiao.controller;

import com.bqiao.domain.Device;
import com.bqiao.service.DeviceService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DeviceController.class)
public class DeviceControllerTests {

    private static final String TEST_PROFILE = "test-profile";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DeviceService service;

    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void givenOneDevice_whenGetDevices_thenReturnJsonArray()
            throws Exception {

        Device dev = new Device();
        dev.setProfile(TEST_PROFILE);

        given(service.getDevices(Optional.empty())).willReturn(Collections.singletonList(dev));

        mvc.perform(get("/device")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].profile", is(TEST_PROFILE)));
    }

    @Test
    public void givenNewDevice_whenCreateDevice_thenReturnCreatedDevice()
            throws Exception {

        Device toCreate = new Device();
        toCreate.setProfile(TEST_PROFILE);
        Device created = new Device();
        created.setProfile(TEST_PROFILE);
        given(service.createDevice(toCreate)).willReturn(created);

        mvc.perform(post("/device")
                .content(mapper.writeValueAsString(toCreate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
