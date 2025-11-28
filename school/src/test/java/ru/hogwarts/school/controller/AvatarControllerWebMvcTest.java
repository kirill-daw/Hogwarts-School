package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvatarController.class)
class AvatarControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllAvatars_shouldReturnPaginatedAvatars() throws Exception {
        Avatar avatar1 = new Avatar();
        avatar1.setId(1L);
        avatar1.setFilePath("/path1");
        avatar1.setFileSize(1024L);
        avatar1.setMediaType("image/jpeg");

        Avatar avatar2 = new Avatar();
        avatar2.setId(2L);
        avatar2.setFilePath("/path2");
        avatar2.setFileSize(2048L);
        avatar2.setMediaType("image/png");

        Page<Avatar> avatarPage = new PageImpl<>(Arrays.asList(avatar1, avatar2),
                PageRequest.of(0, 10), 2);

        when(avatarService.getAllAvatars(0, 10)).thenReturn(avatarPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/avatar")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].filePath").value("/path1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].filePath").value("/path2"));
    }

    @Test
    void getAllAvatars_shouldUseDefaultPagination() throws Exception {
        Page<Avatar> avatarPage = new PageImpl<>(Collections.emptyList(),
                PageRequest.of(0, 10), 0);

        when(avatarService.getAllAvatars(0, 10)).thenReturn(avatarPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/avatar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getAllAvatars_shouldReturnEmptyPageForInvalidPage() throws Exception {
        Page<Avatar> avatarPage = new PageImpl<>(Collections.emptyList(),
                PageRequest.of(999, 10), 0);

        when(avatarService.getAllAvatars(999, 10)).thenReturn(avatarPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/avatar")
                        .param("page", "999")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}