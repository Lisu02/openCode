package com.example.openCode.CompilationModule.Service.DockerHandler.PYTHON3;

import com.example.openCode.CompilationModule.Model.Task.Task;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestArgument;
import com.example.openCode.CompilationModule.Model.Task.TestTask.TestTask;
import com.example.openCode.CompilationModule.Service.DockerHandler.ContainerIdList;
import com.example.openCode.CompilationModule.Service.DockerHandler.DockerConfiguration;
import com.example.openCode.CompilationModule.Service.DockerHandler.MyResultCallback;
import com.example.openCode.CompilationModule.Service.Task.TaskService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class DockerTaskPython3 {

    private static final Logger log = LoggerFactory.getLogger(DockerTaskPython3.class);
    DockerClient dockerClient = DockerConfiguration.getDockerClientInstance();
    String python3ContainerId = ContainerIdList.getPython3ContainerId();

    public void createTaskInContainer(Task task){
        if(TaskService.isTaskReadyForCreation(task) && createDirectoryForTask(task)){
            StringBuilder builder = new StringBuilder();

            generatePythonTestScript(task, builder);

            String catalogName = task.getCatalogName();
            String fileName = task.getFunctionName() + ".py";

            ExecCreateCmdResponse createTaskDockerPython = dockerClient.execCreateCmd(python3ContainerId)
                    .withAttachStdout(true)
                    .withAttachStdin(true)
                    .withAttachStderr(true)
                    .withCmd("sh","-c", "printf '%s' '" + builder + "' > /tmp/" + task.getCatalogName() + "/test.py")
                    .exec();

            MyResultCallback createTaskCallback = new MyResultCallback();

            dockerClient.execStartCmd(createTaskDockerPython.getId()).exec(createTaskCallback);

            try{
                createTaskCallback.awaitCompletion();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            log.info("Creating task in Python3 ->{}", createTaskCallback.getOutput());
        }
    }

    private boolean createDirectoryForTask(Task task){
        ExecCreateCmdResponse execCreateDirectory = dockerClient.execCreateCmd(python3ContainerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withCmd("sh","-c","mkdir -p tmp/" + task.getCatalogName() + " && " +
                        "rm -f tmp/" + task.getCatalogName() +"/* && " +
                        "touch tmp/" + task.getId() + "-" + task.getFunctionName() + "/test.py"
                )
                .exec();
        MyResultCallback createDirectoryCallback = new MyResultCallback();
        dockerClient.execStartCmd(execCreateDirectory.getId()).exec(createDirectoryCallback);
        try{
            createDirectoryCallback.awaitCompletion();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(createDirectoryCallback.getOutput().isEmpty()){
            return true;
        }else {
            log.warn("Unsuccessful creation of a task directory {}, Output: {}",task.getId(),createDirectoryCallback.getOutput());
            return false;
        }
    }

    private void generatePythonTestScript(Task task, StringBuilder builder) {
        builder.append("import importlib.util\n");
        builder.append("import sys\n\n");

        builder.append("OPERATION = \"").append(task.getFunctionName()).append("\"\n\n");

        builder.append("def load_user_function(file_path):\n");
        builder.append("    spec = importlib.util.spec_from_file_location(\"user_module\", file_path)\n");
        builder.append("    user_module = importlib.util.module_from_spec(spec)\n");
        builder.append("    spec.loader.exec_module(user_module)\n");
        builder.append("    return getattr(user_module, \"").append(task.getFunctionName()).append("\")\n\n");

        builder.append("def test(operation, liczba, mnoznik, expected, overall, failed):\n");
        builder.append("    result = operation(liczba, mnoznik)\n");
        builder.append("    overall += 1\n");
        builder.append("    if result == expected:\n");
        builder.append("        print(f\"Test passed: {OPERATION}({liczba}, {mnoznik}) == {expected}\")\n");
        builder.append("    else:\n");
        builder.append("        print(f\"Test failed: {OPERATION}({liczba}, {mnoznik}) == {expected}, got {result} instead\")\n");
        builder.append("        failed += 1\n");
        builder.append("    return overall, failed\n\n");

        builder.append("def main(user_file):\n");
        builder.append("    mnozenie = load_user_function(user_file)\n\n");
        builder.append("    overall = 0\n");
        builder.append("    failed = 0\n\n");

        // Generate test cases based on the task object
        builder.append("    # Run tests on the user-submitted function\n");

        for (TestTask testTask : task.getTestList()) {
            builder.append("    overall, failed = test(mnozenie, ");
            Iterator<TestArgument> testArgumentIterator = testTask.getTestArguments().iterator();
            while (testArgumentIterator.hasNext()) {
                TestArgument testArgument = testArgumentIterator.next();
                builder.append(testArgument.getArgument());
                if (testArgumentIterator.hasNext()) builder.append(", ");
            }
            builder.append(", ").append(testTask.getExpectedValue()).append(", overall, failed)\n");
        }

        builder.append("\n    print(f\"overall: {overall}, failed: {failed}\")\n\n");

        builder.append("if __name__ == \"__main__\":\n");
        builder.append("    if len(sys.argv) != 2:\n");
        builder.append("        print(\"Usage: python test_script.py <user_code_file.py>\")\n");
        builder.append("        sys.exit(1)\n\n");
        builder.append("    user_file = sys.argv[1]\n");
        builder.append("    main(user_file)\n");
    }


}
