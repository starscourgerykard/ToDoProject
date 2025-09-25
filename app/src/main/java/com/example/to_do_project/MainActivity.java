package com.example.to_do_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent; // Importa la clase Intent

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TextInputEditText editTextNewTask;
    private FloatingActionButton buttonAddTask;
    private LinearLayout layoutEmpty;
    private TextView textViewTaskCount;

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
        layoutEmpty = findViewById(R.id.layoutEmpty);
        textViewTaskCount = findViewById(R.id.textViewTaskCount);
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

            // Actualizar contador
            updateEmptyView();

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
        // Actualizar contador de tareas
        int pendingTasks = 0;
        for (Task task : taskList) {
            if (!task.isCompleted()) {
                pendingTasks++;
            }
        }

        String taskCountText = pendingTasks == 0 ? "🎉 Todas las tareas completadas" :
                pendingTasks + " tarea" + (pendingTasks > 1 ? "s" : "") + " pendiente" + (pendingTasks > 1 ? "s" : "");
        textViewTaskCount.setText(taskCountText);

        // Mostrar/ocultar vista vacía
        if (taskList.isEmpty()) {
            recyclerViewTasks.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewTasks.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Guardar tareas cuando la app se pausa
        saveTasks();
    }

    // Nuevo método para ir a la pantalla de bienvenida
    public void goToWelcomeScreen(View view) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
}