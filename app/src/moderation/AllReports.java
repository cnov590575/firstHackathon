package moderation;

import dao.DAO;
import dao.PostDAO;

import java.util.ArrayList;
import java.util.Comparator;

public class AllReports {
    public static ArrayList<Report> allReports;

    public static ArrayList<Report> getAllReports() {
        return allReports;
    }
}
