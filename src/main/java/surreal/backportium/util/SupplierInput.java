package surreal.backportium.util;

public interface SupplierInput<I, O> {
    O get(I input);
}
