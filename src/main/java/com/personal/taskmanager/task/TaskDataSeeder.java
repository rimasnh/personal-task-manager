package com.personal.taskmanager.task;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TaskDataSeeder implements ApplicationRunner {

    private static final int TASK_COUNT = 1000;

    private static final String[] ACTIONS = {
            "Review", "Prepare", "Submit", "Schedule", "Draft", "Update", "Finalize", "Call",
            "Refactor", "Research", "Plan", "Organize", "Reconcile", "Follow up on", "Sync", "Launch"
    };

    private static final String[] OBJECTS = {
            "weekly budget", "doctor appointment", "project roadmap", "quarterly report", "tax documents",
            "kitchen inventory", "travel itinerary", "team retrospective", "resume update", "portfolio case study",
            "car maintenance", "client onboarding", "grocery restock", "performance review", "moving checklist",
            "insurance paperwork", "birthday plan", "meeting agenda", "subscription audit", "fitness schedule"
    };

    private static final String[] CONTEXTS = {
            "before the team review", "with supporting notes attached", "after confirming the latest numbers",
            "and share the outcome with stakeholders", "while the details are still fresh",
            "without blocking the next milestone", "and capture any open questions",
            "so the weekend stays clear", "before the deadline slips", "with a backup option prepared"
    };

    private final TaskRepository taskRepository;

    public TaskDataSeeder(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (taskRepository.count() > 0) {
            return;
        }

        List<Task> tasks = new ArrayList<>(TASK_COUNT);
        tasks.addAll(buildEdgeCaseTasks());

        Random random = new Random(20260428L);
        while (tasks.size() < TASK_COUNT) {
            tasks.add(buildGeneratedTask(tasks.size(), random));
        }

        taskRepository.saveAll(tasks);
    }

    private List<Task> buildEdgeCaseTasks() {
        List<Task> edgeCases = new ArrayList<>();

        edgeCases.add(createTask(
                "Submit annual tax return",
                "Collect W-2s, 1099s, receipts, and send the final packet to the accountant before filing day.",
                LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(2),
                Priority.HIGH,
                TaskStatus.IN_PROGRESS
        ));

        edgeCases.add(createTask(
                "Renew vehicle registration",
                null,
                null,
                LocalDate.now().plusDays(6),
                Priority.MEDIUM,
                TaskStatus.TODO
        ));

        edgeCases.add(createTask(
                "Archive old family photos and receipts in a clearly labeled folder for long-term digital storage",
                "This task has a longer-than-usual title to make sure the UI handles wrapped content gracefully.",
                LocalDate.now().minusDays(60),
                null,
                Priority.LOW,
                TaskStatus.DONE
        ));

        edgeCases.add(createTask(
                "Follow up on recruiter email",
                "Reply with interview availability across multiple time zones.",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1),
                Priority.HIGH,
                TaskStatus.TODO
        ));

        edgeCases.add(createTask(
                "Plan apartment move",
                "Coordinate movers, packing supplies, utility transfer, change-of-address notices, and parking permits.",
                LocalDate.now().minusDays(7),
                LocalDate.now().plusDays(21),
                Priority.HIGH,
                TaskStatus.IN_PROGRESS
        ));

        edgeCases.add(createTask(
                "Clean out inbox",
                "",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(3),
                Priority.LOW,
                TaskStatus.TODO
        ));

        edgeCases.add(createTask(
                "Back up laptop",
                "Start the backup before leaving for the airport and verify the restore drive still mounts correctly.",
                null,
                null,
                Priority.MEDIUM,
                TaskStatus.TODO
        ));

        edgeCases.add(createTask(
                "Replace smoke detector batteries",
                "Include the basement hallway detector that is easy to miss.",
                LocalDate.now().minusDays(90),
                LocalDate.now().minusDays(30),
                Priority.HIGH,
                TaskStatus.DONE
        ));

        edgeCases.add(createTask(
                "Prepare parent-teacher conference notes",
                "Summarize wins, concerns, and questions for both math and reading support.",
                LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(9),
                Priority.MEDIUM,
                TaskStatus.TODO
        ));

        edgeCases.add(createTask(
                "Review quarterly subscriptions",
                "Cancel duplicate services, compare renewal dates, and note any annual discount opportunities.",
                LocalDate.now().minusDays(14),
                LocalDate.now().plusDays(2),
                Priority.MEDIUM,
                TaskStatus.IN_PROGRESS
        ));

        return edgeCases;
    }

    private Task buildGeneratedTask(int index, Random random) {
        String title = ACTIONS[random.nextInt(ACTIONS.length)] + " " + OBJECTS[random.nextInt(OBJECTS.length)];
        String description = title + " " + CONTEXTS[random.nextInt(CONTEXTS.length)] + ".";

        LocalDate startDate = LocalDate.now().minusDays(random.nextInt(120)).plusDays(random.nextInt(75));
        LocalDate dueDate = startDate.plusDays(random.nextInt(45));
        Priority priority = pickPriority(random);
        TaskStatus status = pickStatus(random);

        if (index % 17 == 0) {
            dueDate = null;
        }
        if (index % 23 == 0) {
            startDate = null;
        }
        if (index % 29 == 0) {
            description = null;
        }
        if (index % 31 == 0) {
            dueDate = LocalDate.now().minusDays(random.nextInt(20) + 1);
            status = TaskStatus.TODO;
            priority = Priority.HIGH;
        }
        if (index % 37 == 0) {
            status = TaskStatus.DONE;
            if (startDate == null) {
                startDate = LocalDate.now().minusDays(random.nextInt(40) + 10);
            }
            dueDate = startDate.plusDays(random.nextInt(8));
        }
        if (index % 41 == 0) {
            title = title + " for household, personal, and work coordination";
        }

        return createTask(title, description, startDate, dueDate, priority, status);
    }

    private Priority pickPriority(Random random) {
        int roll = random.nextInt(100);
        if (roll < 20) {
            return Priority.HIGH;
        }
        if (roll < 65) {
            return Priority.MEDIUM;
        }
        return Priority.LOW;
    }

    private TaskStatus pickStatus(Random random) {
        int roll = random.nextInt(100);
        if (roll < 55) {
            return TaskStatus.TODO;
        }
        if (roll < 82) {
            return TaskStatus.IN_PROGRESS;
        }
        return TaskStatus.DONE;
    }

    private Task createTask(
            String title,
            String description,
            LocalDate startDate,
            LocalDate dueDate,
            Priority priority,
            TaskStatus status
    ) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStartDate(startDate);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setStatus(status);
        return task;
    }
}

