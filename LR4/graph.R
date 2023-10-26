library(ggplot2)

data <- data.frame(
  Кол_процессов = c(16, 8, 4, 2, 16, 8, 4, 2),
  Скорость_работы = c(85, 69, 47, 45, 107, 70, 59, 50),
  Тип_процесса = factor(rep(c("Блокирующие", "Не_блокирующие"), each = 4))
)

ggplot(data, aes(x = Кол_процессов, y = Скорость_работы, color = Тип_процесса)) +
  geom_line() +
  geom_point(size = 3, shape = 21, stroke = 1, aes(fill = Тип_процесса)) +
  labs(
    x = "Количество процессов",
    y = "Скорость работы программы, мс.",
    title = "Зависимость времени работы программы от количества процессов при N = 1000000"
  ) +
  scale_color_manual(values = c("Блокирующие" = "red", "Не_блокирующие" = "blue")) +
  scale_fill_manual(values = c("Блокирующие" = "red", "Не_блокирующие" = "blue")) +
  scale_y_continuous(breaks = seq(0, 110, by = 5), minor_breaks = seq(0, 110, by = 5)) +
  theme_minimal()