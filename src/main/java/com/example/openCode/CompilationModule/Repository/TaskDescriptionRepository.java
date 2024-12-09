package com.example.openCode.CompilationModule.Repository;

import com.example.openCode.CompilationModule.Model.Task.TaskDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDescriptionRepository extends JpaRepository<TaskDescription, Long> {
}
