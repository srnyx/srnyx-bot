package xyz.srnyx.srnyx_bot;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class Format extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getLevel() + " >> " + record.getMessage() + "\u001B[0m\n";
    }
}
