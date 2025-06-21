package com.main.docmanager.constants;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConstantsUtil {

	public  static final List<String> ALLOWED_FILE_TYPES = Collections.unmodifiableList(Arrays.asList(
	            "application/pdf",
	            "text/plain",
	            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
	    
	     public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
	     public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
	             .appendPattern("yyyy-MM-dd['T'[ ]HH:mm:ss][.SSS]")
	             .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
	             .toFormatter();
}
