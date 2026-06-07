package me.imbanana.knockdown.data;

import me.imbanana.knockdown.util.KnockdownData;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModDataAttachments {
    public static DataAttachment<KnockdownData> KNOCKDOWN;

    public record DataAttachment<T>(AttachmentGetter<T> getter, AttachmentSetter<T> setter, AttachmentExist exist) {
        public T get(Entity entity) {
            return this.getter.get(entity);
        }

        public void set(Entity entity, T data) {
            setter.set(entity, data);
        }

        public boolean has(Entity entity) {
            return exist.get(entity);
        }

        public T getOrDefault(Entity entity, T _default) {
            return has(entity) ? get(entity) : _default;
        }

        public T getOrSet(Entity entity, T data) {
            if (has(entity)) {
                return get(entity);
            } else {
                set(entity, data);
                return data;
            }
        }

        public void modify(Entity entity, Function<T, T> modifier, T _default) {
            set(entity, modifier.apply(getOrDefault(entity, _default)));
        }
    }

    @FunctionalInterface
    public interface AttachmentGetter<T> {
        T get(Entity entity);
    }

    @FunctionalInterface
    public interface AttachmentSetter<T> {
        void set(Entity entity, T data);
    }

    @FunctionalInterface
    public interface AttachmentExist {
        boolean get(Entity entity);
    }
}
