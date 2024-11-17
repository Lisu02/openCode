package com.example.openCode;

import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestTaskCreation {

    @Test
    public void testTaskFactoryIntNotNull() {
        //given
        Task task = TaskTestFactory.createTaskWithIntValues();

        //when
        ReturnType returnType = task.getReturnType();
        String functionName = task.getFunctionName();
        List<TestTask> testList = task.getTestList();
        List<FunctionArgument> testArgumentList = task.getArgumentList();

        //then
        assertNotNull(returnType);
        assertNotNull(functionName);
        assertNotNull( testList);
        assertNotNull(testArgumentList);
    }

    @Test
    public void testTaskFactoryIntReturnsFunctionName() {
        //given
        Task task = TaskTestFactory.createTaskWithIntValues();

        //when
        String functionName = task.getFunctionName();

        //then
        assertEquals("Addition", functionName);
    }

    @Test
    public void testTaskFactoryIntReturnsProperFunctionArguments() {
        //Given
        Task task = TaskTestFactory.createTaskWithIntValues();

        //When
        List<FunctionArgument> functionArgumentList = task.getArgumentList();

        //Then
        assertNotNull(functionArgumentList,"functionArgumentList is null");
        assertEquals(3, functionArgumentList.size());
        for(FunctionArgument functionArgument : functionArgumentList) {
            assertNotNull(functionArgument,"functionArgument is null");
            assertNotNull(functionArgument.getName(),"Name is null");
            assertNotNull(functionArgument.getType(),"Type is null");
            assertEquals(ReturnType.INT, functionArgument.getType(),"Wrong return type");
        }
    }
}
