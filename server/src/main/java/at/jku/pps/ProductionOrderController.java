package at.jku.pps;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class ProductionOrderController {
    private ProductionOrderRepository repository;

    public ProductionOrderController(final ProductionOrderRepository repository) {
        this.repository = repository;
    }

    private static boolean checkString(String s) {
        return s == null || s.isBlank();
    }

    /* --- return all ProductionOrders --- */
    @GetMapping("/productionOrders")
    public List<ProductionOrder> getProductionOrders() {
        return repository.findAll();
    }

    /* --- return all ProductionOrders sorted by Priority --- */
    @GetMapping("/productionOrdersSorted")
    public List<ProductionOrder> getProductionOrderSorted() {
        return repository.findAll().stream().sorted(Comparator.comparingInt(ProductionOrder::getPriority)).collect(Collectors.toList());
    }

    /* --- insert new ProductionOrder if priority is 0 then insert at end of priority list else insert with given priority --- */
    @PostMapping("/newProductionOrder")
    public ResponseEntity<ProductionOrder> newProductionOrder(String description, Integer priority, String machine) {
        int givenPriority = Objects.requireNonNullElse(priority, 0);

        if (checkString(description) || givenPriority < 0) {
            return ResponseEntity.badRequest().build();
        }

        String givenMachine;
        if (machine == null || machine.isBlank()) {
            givenMachine = null;
        } else {
            givenMachine = machine;
        }

        final ProductionOrder maxPriorityPO = repository.findAll().stream().max(Comparator.comparingInt(ProductionOrder::getPriority)).orElse(null);
        ProductionOrder po;
        if (maxPriorityPO == null) {
            po = new ProductionOrder(description, 1, givenMachine);
        } else if (maxPriorityPO.getPriority() < givenPriority || givenPriority == 0) {
            po = new ProductionOrder(description, maxPriorityPO.getPriority() + 1, givenMachine);
        } else {
            final List<ProductionOrder> pos = repository.findAll().stream().sorted(Comparator.comparingInt(ProductionOrder::getPriority)).collect(Collectors.toList());
            pos.forEach(productionOrder -> {
                if (productionOrder.getPriority() >= givenPriority) {
                    productionOrder.setPriority(productionOrder.getPriority() + 1);
                }
            });
            po = new ProductionOrder(description, givenPriority, givenMachine);
        }
        repository.save(po);
        return ResponseEntity.ok(po);
    }

    /* --- change machine --- */
    @PutMapping("/changeMachine")
    public ResponseEntity<ProductionOrder> changeMachine(Long id, String machine) {
        if (id == null || checkString(machine) || repository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        final ProductionOrder po = repository.findById(id).get();
        po.setMachine(machine);
        repository.save(po);
        return ResponseEntity.ok(po);
    }

    /* --- change priority, if input <= 0 id is set to priority 1, if input > max priority id is set to max priority --- */
    @PutMapping("/changePriority")
    public ResponseEntity<ProductionOrder> changePriority(Long id, Integer priority) {
        if (id == null || priority == null || repository.findById(id).isEmpty() || priority < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (priority == 0){
            priority = repository.findAll().stream().max(Comparator.comparingInt(ProductionOrder::getPriority)).get().getPriority();
        }

        int finalPriority = priority;
        List<ProductionOrder> relevantPos;
        if (repository.findById(id).get().getPriority() < finalPriority) {

            relevantPos = repository.findAll().stream()
                    .filter(c -> c.getPriority() <= finalPriority)
                    .filter(c -> c.getPriority() >= repository.findById(id).get().getPriority())
                    .sorted(Comparator.comparingInt(ProductionOrder::getPriority)).collect(Collectors.toList());
            relevantPos.get(0).setPriority(relevantPos.get(relevantPos.size() - 1).getPriority());
            for (int i = 1; i < relevantPos.size(); i++) {
                relevantPos.get(i).setPriority(relevantPos.get(i).getPriority() - 1);
            }
        } else {
            relevantPos = repository.findAll().stream()
                    .filter(c -> c.getPriority() >= finalPriority)
                    .filter(c -> c.getPriority() <= repository.findById(id).get().getPriority())
                    .sorted(Comparator.comparingInt(ProductionOrder::getPriority)).collect(Collectors.toList());
            Collections.reverse(relevantPos);
            relevantPos.get(0).setPriority(relevantPos.get(relevantPos.size() - 1).getPriority());
            for (int i = 1; i < relevantPos.size(); i++) {
                relevantPos.get(i).setPriority(relevantPos.get(i).getPriority() + 1);
            }
        }
        repository.saveAll(relevantPos);
        return ResponseEntity.ok(relevantPos.get(0));
    }

    @DeleteMapping("/deleteProductionOrder")
    public ResponseEntity deleteProductionOrder(Long id) {
        if (id == null || repository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAllProductionOrders")
    public ResponseEntity deleteAllProductionOrder() {
        repository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
