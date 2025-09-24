package com.example.to_do_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private EditText editTextNewTask;
    private Button buttonAddTask;
    private TextView textViewEmpty;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TodoPrefs";
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Inicializar vistas
        initViews();

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar tareas guardadas
        loadTasks();

        // Configurar listeners
        setupListeners();

        // Actualizar visibilidad
        updateEmptyView();
    }

    private void initViews() {
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        editTextNewTask = findViewById(R.id.editTextNewTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        textViewEmpty = findViewById(R.id.textViewEmpty);
    }

    private void setupRecyclerView() {
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);
    }

    private void setupListeners() {
        buttonAddTask.setOnClickListener(v -> addNewTask());

        // Permitir agregar tarea con Enter
        editTextNewTask.setOnEditorActionListener((v, actionId, event) -> {
            addNewTask();
            return true;
        });
    }

    private void addNewTask() {
        String taskText = editTextNewTask.getText().toString().trim();

        if (TextUtils.isEmpty(taskText)) {
            Toast.makeText(this, "Por favor escribe una tarea", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear nueva tarea
        Task newTask = new Task(taskText, false);
        taskList.add(0, newTask); // Agregar al inicio de la lista

        // Actualizar adapter
        taskAdapter.notifyItemInserted(0);
        recyclerViewTasks.scrollToPosition(0);

        // Limpiar campo de texto
        editTextNewTask.setText("");

        // Guardar en SharedPreferences
        saveTasks();

        // Actualizar visibilidad
        updateEmptyView();

        Toast.makeText(this, "Tarea agregada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskCompleted(int position, boolean isCompleted) {
        if (position >= 0 && position < taskList.size()) {
            Task task = taskList.get(position);
            task.setCompleted(isCompleted);

            // Actualizar vista
            taskAdapter.notifyItemChanged(position);

            // Guardar cambios
            saveTasks();

            String message = isCompleted ? "Tarea completada" : "Tarea marcada como pendiente";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskDeleted(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);

            // Guardar cambios
            saveTasks();

            // Actualizar visibilidad
            updateEmptyView();

            Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTasks() {
        Set<String> taskSet = new HashSet<>();
        for (Task task : taskList) {
            taskSet.add(task.toStorageString());
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(TASKS_KEY, taskSet);
        editor.apply();
    }

    private void loadTasks() {
        Set<String> taskSet = sharedPreferences.getStringSet(TASKS_KEY, new HashSet<>());

        taskList.clear();
        for (String taskString : taskSet) {
            Task task = Task.fromStorageString(taskString);
            if (task != null) {
                taskList.add(task);
            }
        }

        // Ordenar por ID (las más recientes primero)
        taskList.sort((t1, t2) -> Long.compare(t2.getId(), t1.getId()));

        taskAdapter.notifyDataSetChanged();
    }

    private void updateEmptyView() {
        if (taskList.isEmpty()) {
            recyclerViewTasks.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewTasks.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Guardar tareas cuando la app se pausa
        saveTasks();
    }
}