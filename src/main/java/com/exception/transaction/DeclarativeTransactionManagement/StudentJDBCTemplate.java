package com.exception.transaction.DeclarativeTransactionManagement;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class StudentJDBCTemplate implements StudentDAO {
    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }
    public void create(String name, Integer age, Integer marks, Integer year){
        try {
            String SQL1 = "insert into studentRecords (name, age) values (?, ?)";
            jdbcTemplateObject.update( SQL1, name, age);

            // Get the latest student id to be used in Marks table

            int sid = jdbcTemplateObject.queryForObject("select max(id) from studentRecords", Integer.class );

            String SQL3 = "insert into studentRecordsMarks(sid, marks, year) " + "values (?, ?, ?)";
            jdbcTemplateObject.update( SQL3, sid, marks, year);
            System.out.println("Created Name = " + name + ", Age = " + age);

            // to simulate the exception.
            throw new RuntimeException("simulate Error condition") ;
        }
        catch (DataAccessException e) {
            System.out.println("Error in creating record, rolling back");
            throw e;
        }
    }
    public List<StudentMarks> listStudents() {
        String SQL = "select * from studentRecords, studentRecordsMarks where studentRecords.id = studentRecordsMarks.sid";
        List <StudentMarks> studentMarks = jdbcTemplateObject.query(SQL,
                new StudentMarksMapper());
        return studentMarks;
    }
}
