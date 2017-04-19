package ar.fiuba.taller;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.googlecode.objectify.annotation.Ignore;

import java.lang.String;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.text.ParseException;

@Entity
public class Function {
  @Id private Long id;

  private String name;
  @Index private Long hour;

  public Function(String name) {
    this.name = name;
    this.hour = new Long(0);
  }

  public Function() {
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Long getHour() {
    return hour;
  }

  public void incHour() {
    hour++;
  }

}
