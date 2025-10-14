package com.nitroxen.demo.repository;

import com.nitroxen.demo.entity.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WorkerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkerRepository workerRepository;

    private Worker testWorker;

    @BeforeEach
    void setUp() {
        testWorker = new Worker();
        testWorker.setName("John Doe");
        testWorker.setEmail("john.doe@example.com");
        testWorker.setPhoneNumber("+1234567890");
        testWorker.setStatus(Worker.WorkerStatus.ACTIVE);
    }

    @Test
    public void testFindByPhoneNumber_Success() {
        // Arrange
        entityManager.persistAndFlush(testWorker);

        // Act
        Optional<Worker> found = workerRepository.findByPhoneNumber("+1234567890");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
        assertEquals("+1234567890", found.get().getPhoneNumber());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    public void testFindByPhoneNumber_NotFound() {
        // Act
        Optional<Worker> found = workerRepository.findByPhoneNumber("+9999999999");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindByEmail_Success() {
        // Arrange
        entityManager.persistAndFlush(testWorker);

        // Act
        Optional<Worker> found = workerRepository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
        assertEquals("+1234567890", found.get().getPhoneNumber());
    }

    @Test
    public void testFindByEmail_NotFound() {
        // Act
        Optional<Worker> found = workerRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    public void testSaveWorker() {
        // Act
        Worker saved = workerRepository.save(testWorker);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getName());
        assertEquals("+1234567890", saved.getPhoneNumber());
        assertEquals(Worker.WorkerStatus.ACTIVE, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    public void testUniquePhoneNumberConstraint() {
        // Arrange
        entityManager.persistAndFlush(testWorker);

        Worker duplicateWorker = new Worker();
        duplicateWorker.setName("Jane Doe");
        duplicateWorker.setEmail("jane.doe@example.com");
        duplicateWorker.setPhoneNumber("+1234567890"); // Same phone number

        // Act & Assert
        assertThrows(Exception.class, () -> {
            workerRepository.save(duplicateWorker);
            entityManager.flush();
        });
    }

    @Test
    public void testUniqueEmailConstraint() {
        // Arrange
        entityManager.persistAndFlush(testWorker);

        Worker duplicateWorker = new Worker();
        duplicateWorker.setName("Jane Doe");
        duplicateWorker.setEmail("john.doe@example.com"); // Same email
        duplicateWorker.setPhoneNumber("+9876543210");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            workerRepository.save(duplicateWorker);
            entityManager.flush();
        });
    }
}
