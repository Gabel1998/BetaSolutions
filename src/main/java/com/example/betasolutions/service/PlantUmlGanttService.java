package com.example.betasolutions.service;

////====================================
////->   Official Gantt Chart Service  =
////->   DO NOT CHANGE THIS FILE       =
////->  Methods gotten from PlantUML   =
////               :-)                 =
////====================================
//Comments from Co-Pilot for better understanding

import com.example.betasolutions.model.SubProject;
import com.example.betasolutions.model.Task;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PlantUmlGanttService {

    public byte[] generateGantt(SubProject subProject, List<Task> tasks) throws IOException {
        StringBuilder uml = new StringBuilder();
        uml.append("@startgantt\n");

        // Title/Header
        uml.append("' Subproject: ").append(subProject.getName()).append("\n");
        uml.append("Project starts ").append(subProject.getStartDate()).append("\n\n");

        // Subproject bar
        uml.append("[")
                .append(subProject.getName())
                .append("] starts ")
                .append(subProject.getStartDate())
                .append(" and ends ")
                .append(subProject.getEndDate())
                .append("\n");

        // Loop through tasks
        for (Task task : tasks) {
            String taskName = task.getName() != null ? task.getName() : "Unnamed Task";
            LocalDate start = task.getStartDate();
            LocalDate end = task.getEndDate();

            if (start == null || end == null) continue; // Skip tasks with invalid dates

            long duration = ChronoUnit.DAYS.between(start, end);

            // Calculate percent complete, handling potential division by zero
            int percentComplete;
            try {
                percentComplete = task.getPercentComplete();
            } catch (ArithmeticException e) {
                percentComplete = 0; // Default to 0% if division by zero occurs
            }

            String assignedTo = task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned";

            // Diagram line
            uml.append("[")
                    .append(taskName)
                    .append(" – ")
                    .append(duration).append("d – ")
                    .append(percentComplete).append("% – ")
                    .append(assignedTo)
                    .append("] starts ").append(start)
                    .append(" and ends ").append(end)
                    .append("\n");
        }

        uml.append("@endgantt");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        SourceStringReader reader = new SourceStringReader(uml.toString());
        reader.generateImage(os);
        return os.toByteArray();
    }
}