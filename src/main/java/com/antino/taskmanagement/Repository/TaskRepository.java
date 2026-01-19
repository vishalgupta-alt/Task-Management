package com.antino.taskmanagement.Repository;

import com.antino.taskmanagement.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByCreatedBy(String email);

    List<Task> findByStatus(String status);

    List<Task> findByPriority(String priority);

    List<Task> findByTitleContainingIgnoreCase(String keyword);
    Optional<Task> findById(Long id);

}
