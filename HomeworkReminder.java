import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 作业提交提醒器
 * 功能：添加作业、显示所有作业、检查即将截止的作业
 */
public class HomeworkReminder {

    // 定义作业实体类
    static class Homework {
        private String name;        // 作业名称
        private LocalDateTime deadline; // 截止时间

        public Homework(String name, LocalDateTime deadline) {
            this.name = name;
            this.deadline = deadline;
        }

        public String getName() {
            return name;
        }

        public LocalDateTime getDeadline() {
            return deadline;
        }

        // 计算距离截止时间还有多少小时
        public long getHoursToDeadline() {
            return ChronoUnit.HOURS.between(LocalDateTime.now(), deadline);
        }
    }

    private static List<Homework> homeworkList = new ArrayList<>();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== 欢迎使用作业提交提醒器 ===");
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // 吸收换行符

            switch (choice) {
                case 1:
                    addHomework();
                    break;
                case 2:
                    showAllHomework();
                    break;
                case 3:
                    checkReminders();
                    break;
                case 0:
                    System.out.println("程序已退出，感谢使用！");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("输入错误，请重新选择！");
            }
        }
    }

    // 打印菜单
    private static void printMenu() {
        System.out.println("\n请选择操作：");
        System.out.println("1. 添加新作业");
        System.out.println("2. 查看所有作业");
        System.out.println("3. 检查提醒（即将截止）");
        System.out.println("0. 退出程序");
        System.out.print("输入选项：");
    }

    // 添加作业
    private static void addHomework() {
        System.out.print("请输入作业名称：");
        String name = scanner.nextLine();

        System.out.print("请输入截止时间（格式：2026-02-28 23:59）：");
        String timeStr = scanner.nextLine();

        try {
            LocalDateTime deadline = LocalDateTime.parse(timeStr, formatter);
            homeworkList.add(new Homework(name, deadline));
            System.out.println("✅ 作业添加成功！");
        } catch (Exception e) {
            System.out.println("❌ 时间格式错误，请重新添加！");
        }
    }

    // 查看所有作业
    private static void showAllHomework() {
        if (homeworkList.isEmpty()) {
            System.out.println("📭 暂无作业记录。");
            return;
        }

        System.out.println("\n=== 我的作业列表 ===");
        for (int i = 0; i < homeworkList.size(); i++) {
            Homework hw = homeworkList.get(i);
            System.out.printf("%d. %s | 截止时间：%s%n", 
                i + 1, hw.getName(), hw.getDeadline().format(formatter));
        }
    }

    // 检查提醒（24小时内截止的作业）
    private static void checkReminders() {
        System.out.println("\n=== ⚠️  即将截止提醒 ===");
        boolean hasUrgent = false;

        for (Homework hw : homeworkList) {
            long hours = hw.getHoursToDeadline();
            if (hours > 0 && hours <= 24) {
                System.out.printf("【紧急】%s 将在 %d 小时后截止！%n", hw.getName(), hours);
                hasUrgent = true;
            } else if (hours <= 0) {
                System.out.printf("【逾期】%s 已经截止！%n", hw.getName());
                hasUrgent = true;
            }
        }

        if (!hasUrgent) {
            System.out.println("🎉 暂无即将截止的作业，放心摸鱼！");
        }
    }
}