package lab.cadl.analysis.behavior.engine.event.repository.sqlite;


import lab.cadl.analysis.behavior.engine.event.Event;
import lab.cadl.analysis.behavior.engine.event.EventAssignment;
import lab.cadl.analysis.behavior.engine.event.EventCriteria;
import lab.cadl.analysis.behavior.engine.event.EventRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class SqliteEventRepository implements EventRepository {
    private static final Logger logger = LoggerFactory.getLogger(SqliteEventRepository.class);

    private static final String EVENT_NUMBER = "eventno";
    private static final String EVENT_TYPE = "eventtype";
    private static final String TIMESTAMP = "timestamp";
    private static final String TIMESTAMP_USEC = "timestampusec";
    private static final String ORIGIN = "origin";
    private static final String DEFAULT_DB = "events";
    private static final String[] requiredColumns = new String[]{
            EVENT_NUMBER, EVENT_TYPE, TIMESTAMP, TIMESTAMP_USEC, ORIGIN
    };
    private static final List<String> requiredColumnList = Arrays.asList(requiredColumns);
    private ConcurrentHashMap<String, List<Event>> queryCache = new ConcurrentHashMap<>();

    private Connection connection;

    public SqliteEventRepository(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    @Override
    public List<Event> query(List<String> eventIds) {
        return executeQuery(String.format("select * from %s where eventno in (%s)", DEFAULT_DB, StringUtils.join(eventIds, ", ")), null);
    }

    @Override
    public List<Event> list(String eventType) {
        return executeQuery(String.format("select * from %s", eventType), null);
    }

    private List<Event> executeQuery(String sql, List<EventAssignment> assignmentList) {
        List<Event> events = queryCache.get(sql);
        if (events != null) {
            logger.debug("use sql cache for: {}", sql);
            return queryCache.get(sql);
        }

        try {
            Statement statement = connection.createStatement();
            try (ResultSet rs = statement.executeQuery(sql)) {

                List<String> columns = queryColumnNames(rs);
                checkRequiredColumns(columns, requiredColumns);
                columns.removeAll(requiredColumnList);

                events = new ArrayList<>();
                while (rs.next()) {
                    Event event = parseEvent(rs, columns, assignmentList);
                    events.add(event);
                }

                queryCache.put(sql, events);
                logger.debug("query result count {}", events.size());
                return events;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<Event> query(String eventType, List<EventCriteria> criteriaList, List<EventAssignment> assignmentList) {
        String sql = String.format("select * from %s", eventType);
        if (!criteriaList.isEmpty()) {
            sql += " where ";
            // TODO 处理不同类型的SQL条件
            boolean first = true;
            for (EventCriteria criteria : criteriaList) {
                if (first) {
                    first = false;
                } else {
                    sql += " and ";
                }

                sql += criteria.getName() + " " + criteria.getOp().getOp() + " " + criteria.getValue().toString();
            }
        }

        logger.debug("sqlite query string: {}", sql);
        return executeQuery(sql, assignmentList);
    }

    private Event parseEvent(ResultSet rs, List<String> columns, List<EventAssignment> assignmentList) throws SQLException {
        long number = rs.getLong(EVENT_NUMBER);
        String type = rs.getString(EVENT_TYPE);
        long seconds = rs.getLong(TIMESTAMP);
        long us = rs.getInt(TIMESTAMP_USEC);
        Instant timestamp = Instant.ofEpochSecond(seconds, us * 1000);
        String origin = rs.getString(ORIGIN);
        Event event = new Event(number, timestamp, type, origin, columns);

        for (String name : columns) {
            event.attr(name, rs.getObject(name));
        }

        if (assignmentList != null) {
            for (EventAssignment assignment : assignmentList) {
                event.attr(assignment.getName(), rs.getObject(assignment.getPosition() + 4));
            }
        }

        return event;
    }

    private List<String> queryColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columns.add(metaData.getColumnName(i));
        }
        return columns;

    }

    private void checkRequiredColumns(List<String> columns, String... requiredColumns) {
        for (String name : requiredColumns) {
            if (!columns.contains(name)) {
                throw new RuntimeException("列" + name + "不存在");
            }
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
                // ignore any error
            }

            connection = null;
        }
    }

    public static void main(String[] args) throws SQLException {
        try(EventRepository repository = new SqliteEventRepository("/Users/lirui/IdeaProjects/analysis-parent/data/sqlite/dnsflows_5000rec.sqlite")) {
            for (Event event : repository.list("PACKET_DNS")) {
                System.out.println(event);
            }
        }
    }
}
