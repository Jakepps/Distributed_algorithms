library(ggplot2)

data <- data.frame(
  Кол_процессов = c(16, 8, 4, 2, 16, 8, 4, 2),
  Скорость_работы = c(2065, 1670, 1552, 1413, 2000, 1502, 1312, 1419),
  Тип_процесса = factor(rep(c("N=32768", "N=1024"), each = 4))
)

ggplot(data, aes(x = Кол_процессов, y = Скорость_работы, color = Тип_процесса)) +
  geom_line() +
  geom_point(size = 3, shape = 21, stroke = 1, aes(fill = Тип_процесса)) +
  labs(
    x = "Количество процессов",
    y = "Скорость работы программы, мс.",
    title = "Зависимость времени работы программы от количества вершин графа"
  ) +
  scale_color_manual(values = c("N=32768" = "red", "N=1024" = "blue")) +
  scale_fill_manual(values = c("N=1024" = "red", "N=1024" = "blue")) +
  #scale_y_continuous(breaks = seq(0, 110, by = 5), minor_breaks = seq(0, 110, by = 5)) +
  theme_minimal()