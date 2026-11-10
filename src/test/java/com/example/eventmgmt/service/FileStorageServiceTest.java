package com.example.eventmgmt.service;

import com.example.eventmgmt.dto.FileUploadResponse;
import com.example.eventmgmt.entity.Event;
import com.example.eventmgmt.exception.BusinessRuleException;
import com.example.eventmgmt.exception.ResourceNotFoundException;
import com.example.eventmgmt.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {
    @Mock EventRepository eventRepo;
    @TempDir Path tempDir;

    FileStorageService service;
    Event event;

    @BeforeEach
    void setUp() {
        service = new FileStorageService(eventRepo, tempDir.toString());
        event = new Event();
        event.setId(1L);
    }

    @Test
    void store_savesPdfAndUpdatesEvent() {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        MockMultipartFile file = new MockMultipartFile("file", "any-name.pdf", "application/pdf", "hello".getBytes());

        FileUploadResponse res = service.store(1L, file);

        assertEquals("event-1-brochure.pdf", res.fileName());
        assertTrue(Files.exists(tempDir.resolve("event-1-brochure.pdf")));
        assertEquals("event-1-brochure.pdf", event.getBrochureFileName());
        verify(eventRepo).save(event);
    }

    @Test
    void store_throws_whenFileTypeNotAllowed() {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        MockMultipartFile exe = new MockMultipartFile("file", "x.exe", "application/octet-stream", "x".getBytes());
        assertThrows(BusinessRuleException.class, () -> service.store(1L, exe));
    }

    @Test
    void store_throws_whenFileEmpty() {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        MockMultipartFile empty = new MockMultipartFile("file", "a.pdf", "application/pdf", new byte[0]);
        assertThrows(BusinessRuleException.class, () -> service.store(1L, empty));
    }

    @Test
    void load_returnsStoredFile() throws IOException {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        service.store(1L, new MockMultipartFile("file", "b.png", "image/png", "12345".getBytes()));

        FileStorageService.StoredFile stored = service.load(1L);

        assertTrue(stored.resource().exists());
        assertEquals(5, stored.resource().contentLength());
        assertEquals("image/png", stored.contentType());
    }

    @Test
    void load_throws_whenNoBrochureUploaded() {
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        assertThrows(ResourceNotFoundException.class, () -> service.load(1L));
    }
}
