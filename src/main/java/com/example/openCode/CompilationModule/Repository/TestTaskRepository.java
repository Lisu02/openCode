package com.example.openCode.CompilationModule.Repository;

import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTaskRepository extends JpaRepository<TestTask, Long> {
}
