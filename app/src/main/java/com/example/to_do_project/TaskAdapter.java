package com.example.to_do_project;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskActionListener listener;

    // Interface para manejar acciones en las tareas
    public interface OnTaskActionListener {
        void onTaskCompleted(int position, boolean isCompleted);
        void onTaskDeleted(int position);
    }

    public TaskAdapter(List<Task> taskList, OnTaskActionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.textViewTask.setText(task.getText());
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        // Aplicar estilo tachado si está completada
        if (task.isCompleted()) {
            holder.textViewTask.setPaintFlags(holder.textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewTask.setAlpha(0.6f);
        } else {
            holder.textViewTask.setPaintFlags(holder.textViewTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewTask.setAlpha(1.0f);
        }

        // Listener para checkbox
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskCompleted(position, isChecked);
            }
        });

        // Listener para botón eliminar
        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskDeleted(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // ViewHolder interno
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTask;
        CheckBox checkBoxCompleted;
        ImageButton buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    // Método para actualizar la lista
    public void updateTaskList(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }
}