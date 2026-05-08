package moderation;

import dao.DAO;
import dao.PostDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class AllReports {
    public static ArrayList<Report> allReports;


    public int timesReported(UUID message) {
        int reportedtimes = 0;
        for (Report report : allReports) {
            if (report.message().equals(message)) {
                reportedtimes += 1;
            }
        }
        return reportedtimes;
    }
}
