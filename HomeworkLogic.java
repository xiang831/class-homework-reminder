import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomeworkLogic {

    // 1. 计算距离截止时间的剩余时间
    public static String getTimeRemaining(LocalDateTime dueDate) {
        LocalDateTime now = LocalDateTime.now();
        if (dueDate.isBefore(now)) {
            return "已过期";
        }
        Duration duration = Duration.between(now, dueDate);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        return days + "天" + hours + "小时" + minutes + "分钟";
    }

    // 2. 按截止时间升序排序
    public static List<Homework> sortHomeworksByDueDate(List<Homework> homeworks) {
        return homeworks.stream()
                .sorted(Comparator.comparing(Homework::getDueDate))
                .collect(Collectors.toList());
    }

    // 3. 判断是否需要提醒（到期前1天）
    public static boolean shouldRemind(LocalDateTime dueDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayLater = now.plusDays(1);
        return dueDate.isAfter(now) && dueDate.isBefore(oneDayLater);
    }
}
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeworkStorage {
    private static final String STORAGE_FILE = "homeworks.json";
    private static final Gson gson = new Gson();

    // 读取所有作业
    public static List<Homework> loadHomeworks() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Homework>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 保存所有作业
    public static void saveHomeworks(List<Homework> homeworks) {
        try (Writer writer = new FileWriter(STORAGE_FILE)) {
            gson.toJson(homeworks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 添加新作业
    public static void addHomework(Homework homework) {
        List<Homework> homeworks = loadHomeworks();
        homework.setId(UUID.randomUUID().toString());
        homework.setCreateTime(LocalDateTime.now());
        homeworks.add(homework);
        saveHomeworks(homeworks);
    }

    // 更新作业完成状态
    public static void toggleHomeworkCompleted(String id) {
        List<Homework> homeworks = loadHomeworks();
        for (Homework hw : homeworks) {
            if (hw.getId().equals(id)) {
                hw.setCompleted(!hw.isCompleted());
                break;
            }
        }
        saveHomeworks(homeworks);
    }

    // 删除作业
    public static void deleteHomework(String id) {
        List<Homework> homeworks = loadHomeworks();
        homeworks.removeIf(hw -> hw.getId().equals(id));
        saveHomeworks(homeworks);
    }
}
import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderService {

    public static void checkReminders() {
        List<Homework> homeworks = HomeworkStorage.loadHomeworks();
        List<Homework> dueSoonHomeworks = homeworks.stream()
                .filter(hw -> !hw.isCompleted() && HomeworkLogic.shouldRemind(hw.getDueDate()))
                .collect(Collectors.toList());

        if (!dueSoonHomeworks.isEmpty()) {
            StringBuilder sb = new StringBuilder("提醒：以下作业将在明天截止，请尽快完成：\n");
            for (Homework hw : dueSoonHomeworks) {
                sb.append("- ").append(hw.getTitle()).append("\n");
            }
            // 弹窗提醒
            JOptionPane.showMessageDialog(null, sb.toString(), "作业截止提醒", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 程序启动时检查提醒
    public static void main(String[] args) {
        checkReminders();
        // 也可以设置定时任务，每小时检查一次
        // ...
    }
}
