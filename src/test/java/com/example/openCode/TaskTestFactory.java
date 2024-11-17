package com.example.openCode;

import com.example.openCode.CompilationModule.Model.Task.FunctionArgument;
import com.example.openCode.CompilationModule.Model.Task.ReturnType;
import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;

import java.util.ArrayList;
import java.util.List;

public class TaskTestFactory {

    public static Task createTaskWithIntValues(){
        // TASK -> TASKARGUMENTS
        // TEST -> TESTARGUMENTS

        List<FunctionArgument> functionArguments = new ArrayList<>();
        functionArguments.add(new FunctionArgument(0, ReturnType.INT,"num1",null));
        functionArguments.add(new FunctionArgument(1, ReturnType.INT,"num2",null));
        functionArguments.add(new FunctionArgument(2, ReturnType.INT,"num3",null));

        List<TestArgument> testArguments = new ArrayList<>();
        testArguments.add(new TestArgument(0,ReturnType.INT,"10",null));
        testArguments.add(new TestArgument(1,ReturnType.INT,"20",null));
        testArguments.add(new TestArgument(2,ReturnType.INT,"30",null));

        TestTask testTask = TestTask.builder()
                .id(0)
                .expectedValue("60")
                .testArguments(testArguments)
                .build();
        List<TestTask> testTasks = new ArrayList<>();
        testTasks.add(testTask);

        Task task = Task.builder()
                .id(0)
                .argumentList(functionArguments)
                .returnType(ReturnType.INT)
                .testList(testTasks)
                .functionName("Addition")
                .build();

        testTask.setTask(task);
        testArguments.forEach(it -> it.setTestTask(testTask));
        functionArguments.forEach(it -> it.setTask(task));

        return task;
    }

    public static Task createTaskWithCharValues(){
        return null;
    }

    public static Task createTaskWithStringValues(){
        return null;
    }


    public static Task createTaskWithNonArrayValues(){
        return null;
    }

    public static Task createTaskWithArrayValues(){
        return  null;
    }
}
