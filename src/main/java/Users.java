
public class Users extends Person {
    private double budget;

    public Users(String name, int age, double budget) {
        super(name, age);
        this.budget = budget;
    }

    public double getBudget() {
        return this.budget;
    }

    public boolean pay(double amount) {
        if (this.budget >= amount) {
            this.budget -= amount;
            return true;
        } else {
            return false;
        }
    }
}
