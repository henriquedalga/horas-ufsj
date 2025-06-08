package IntegrandoDrive.model;

public class Student {
    private String name;
    private boolean submitted;

    // construtor original
    public Student(String name) {
        this.name = name;
        this.submitted = false;
    }

    public Student(String name, String submittedAsString) {
        this.name = name;
        this.submitted = Boolean.parseBoolean(submittedAsString);
    }

    public String getName() { return name; }
    public boolean hasSubmitted() { return submitted; }
    public void setSubmitted(boolean submitted) { this.submitted = submitted; }
}