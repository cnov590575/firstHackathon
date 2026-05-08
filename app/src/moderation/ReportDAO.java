package moderation;

import dao.DAO;

import java.util.Comparator;

public class ReportDAO extends DAO<Report> {

    protected ReportDAO(Comparator<Report> comparator) {
        super(comparator);
    }

}
