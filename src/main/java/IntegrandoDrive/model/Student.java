package IntegrandoDrive.model;

public class Student {
    private String name;
    private boolean submitted;

    public Student(String name) {
        this.name = name;
        this.submitted = false;
    }
    public String getName() { return name; }
    public boolean hasSubmitted() { return submitted; }
    public void setSubmitted(boolean submitted) { this.submitted = submitted; }
}