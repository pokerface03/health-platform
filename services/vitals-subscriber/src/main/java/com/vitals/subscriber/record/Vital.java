
package com.vitals.subscriber.record;



import java.sql.Timestamp;



public record Vital ( Integer id, String user_id, Timestamp timestamp, String metric_name, Double value){}