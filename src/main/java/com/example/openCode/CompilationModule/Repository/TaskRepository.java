package com.example.openCode.CompilationModule.Repository;

import com.example.openCode.CompilationModule.Model.Task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
}
