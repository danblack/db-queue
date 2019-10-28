package ru.yandex.money.common.dbqueue.internal.runner;

import ru.yandex.money.common.dbqueue.api.QueueConsumer;
import ru.yandex.money.common.dbqueue.api.TaskRecord;
import ru.yandex.money.common.dbqueue.internal.processing.QueueProcessingStatus;
import ru.yandex.money.common.dbqueue.internal.processing.TaskPicker;
import ru.yandex.money.common.dbqueue.internal.processing.TaskProcessor;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Базовая реализация обработчика задач очереди
 *
 * @author Oleg Kandaurov
 * @since 27.08.2017
 */
public class BaseQueueRunner implements QueueRunner {

    @Nonnull
    private final TaskPicker taskPicker;
    @Nonnull
    private final TaskProcessor taskProcessor;
    @Nonnull
    private final Executor executor;

    /**
     * Конструктор
     *
     * @param taskPicker    выборщик задачи
     * @param taskProcessor обработчик задачи
     * @param executor      исполнитель задачи
     */
    BaseQueueRunner(@Nonnull TaskPicker taskPicker,
                    @Nonnull TaskProcessor taskProcessor,
                    @Nonnull Executor executor) {
        this.taskPicker = Objects.requireNonNull(taskPicker);
        this.taskProcessor = Objects.requireNonNull(taskProcessor);
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    @Nonnull
    public QueueProcessingStatus runQueue(@Nonnull QueueConsumer queueConsumer) {
        TaskRecord taskRecord = taskPicker.pickTask(queueConsumer);
        if (taskRecord == null) {
            return QueueProcessingStatus.SKIPPED;
        }
        executor.execute(() -> taskProcessor.processTask(queueConsumer, taskRecord));
        return QueueProcessingStatus.PROCESSED;
    }
}
