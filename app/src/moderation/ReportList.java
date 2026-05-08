package moderation;

import dao.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportList {
    List<Report> reportlist;
    Map<Report, Integer> reportCount;

    public ReportList(List<Report> reports, Map<Report, Integer> reportCount) {
        this.reportlist = reports;
        this.reportCount = reportCount;
    }

    public List<Report> getReports() {
        return reportlist;
    }
    public Map<Report, Integer> getReportCount() {
        return reportCount;
    }
    public void addReport(Report report) {
        reportlist.add(report);
    }
}
