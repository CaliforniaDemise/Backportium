package surreal.backportium.util;

@FunctionalInterface
public interface SupplierInput<I, O> {
    O get(I input);
}
