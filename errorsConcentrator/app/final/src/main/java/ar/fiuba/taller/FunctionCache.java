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
public class FunctionCache {
  @Id private Long id;

  private String name;
  @Index private Long count;

  public FunctionCache(String name)  throws ParseException {
    this.name = name;
    this.count = new Long(1);
  }

  public FunctionCache() {
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Long getCount() {
    return count;
  }

  public void incCount() {
    count++;
  }

  public void reset() {
    count = new Long(0);
  }

}
