package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Task type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Tasks", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byteam", fields = {"teamId","name"})
public final class Task implements Model {
  public static final QueryField ID = field("Task", "id");
  public static final QueryField NAME = field("Task", "name");
  public static final QueryField DESCRIPTION = field("Task", "description");
  public static final QueryField STATE = field("Task", "state");
  public static final QueryField DUE_DATE = field("Task", "dueDate");
  public static final QueryField TEAM_PERSON = field("Task", "teamId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="TaskState") TaskState state;
  private final @ModelField(targetType="AWSDate") Temporal.Date dueDate;
  private final @ModelField(targetType="Team") @BelongsTo(targetName = "teamId", targetNames = {"teamId"}, type = Team.class) Team teamPerson;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getDescription() {
      return description;
  }
  
  public TaskState getState() {
      return state;
  }
  
  public Temporal.Date getDueDate() {
      return dueDate;
  }
  
  public Team getTeamPerson() {
      return teamPerson;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Task(String id, String name, String description, TaskState state, Temporal.Date dueDate, Team teamPerson) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.state = state;
    this.dueDate = dueDate;
    this.teamPerson = teamPerson;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Task task = (Task) obj;
      return ObjectsCompat.equals(getId(), task.getId()) &&
              ObjectsCompat.equals(getName(), task.getName()) &&
              ObjectsCompat.equals(getDescription(), task.getDescription()) &&
              ObjectsCompat.equals(getState(), task.getState()) &&
              ObjectsCompat.equals(getDueDate(), task.getDueDate()) &&
              ObjectsCompat.equals(getTeamPerson(), task.getTeamPerson()) &&
              ObjectsCompat.equals(getCreatedAt(), task.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), task.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getDescription())
      .append(getState())
      .append(getDueDate())
      .append(getTeamPerson())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Task {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("state=" + String.valueOf(getState()) + ", ")
      .append("dueDate=" + String.valueOf(getDueDate()) + ", ")
      .append("teamPerson=" + String.valueOf(getTeamPerson()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Task justId(String id) {
    return new Task(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      description,
      state,
      dueDate,
      teamPerson);
  }
  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    Task build();
    BuildStep id(String id);
    BuildStep description(String description);
    BuildStep state(TaskState state);
    BuildStep dueDate(Temporal.Date dueDate);
    BuildStep teamPerson(Team teamPerson);
  }
  

  public static class Builder implements NameStep, BuildStep {
    private String id;
    private String name;
    private String description;
    private TaskState state;
    private Temporal.Date dueDate;
    private Team teamPerson;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String description, TaskState state, Temporal.Date dueDate, Team teamPerson) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.state = state;
      this.dueDate = dueDate;
      this.teamPerson = teamPerson;
    }
    
    @Override
     public Task build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Task(
          id,
          name,
          description,
          state,
          dueDate,
          teamPerson);
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep state(TaskState state) {
        this.state = state;
        return this;
    }
    
    @Override
     public BuildStep dueDate(Temporal.Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }
    
    @Override
     public BuildStep teamPerson(Team teamPerson) {
        this.teamPerson = teamPerson;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String description, TaskState state, Temporal.Date dueDate, Team teamPerson) {
      super(id, name, description, state, dueDate, teamPerson);
      Objects.requireNonNull(name);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder state(TaskState state) {
      return (CopyOfBuilder) super.state(state);
    }
    
    @Override
     public CopyOfBuilder dueDate(Temporal.Date dueDate) {
      return (CopyOfBuilder) super.dueDate(dueDate);
    }
    
    @Override
     public CopyOfBuilder teamPerson(Team teamPerson) {
      return (CopyOfBuilder) super.teamPerson(teamPerson);
    }
  }
  

  public static class TaskIdentifier extends ModelIdentifier<Task> {
    private static final long serialVersionUID = 1L;
    public TaskIdentifier(String id) {
      super(id);
    }
  }
  
}
