package at.jku.pps;

import com.sun.istack.NotNull;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
public class ProductionOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "priority", nullable = false)
    private int priority;

    @Column(name = "machine")
    private String machine;

    public ProductionOrder() {
    }

    public ProductionOrder(String description, int priority, String machine) {
        this.description = description;
        this.priority = priority;
        this.machine = machine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        return "ProductionOrder{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", machine='" + machine + '\'' +
                '}';
    }
}
