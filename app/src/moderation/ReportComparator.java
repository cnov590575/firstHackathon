package moderation;

import java.util.Comparator;

public class ReportComparator implements Comparator<Report> {
    private static ReportComparator instance;

    public static ReportComparator getInstance() {
        if (instance == null) {
            instance = new ReportComparator();
        }
        return instance;
    }
    private ReportComparator() {}



    @Override
    public int compare(Report o1, Report o2) {
        int delta = Long.compare(o1.timestamp(), o2.timestamp());
        if (delta != 0) return delta;

        delta = o1.message().compareTo(o2.message());
        if (delta != 0) return delta;

        delta = o1.user().compareTo(o2.user());
        return delta;
    }
}
