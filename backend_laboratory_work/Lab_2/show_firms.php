<?php
require_once("mydb.php");

    $sql = "SELECT * FROM firms";

    $result = $connection->query($sql);

    if ($result->num_rows > 0) {
        echo "<table border='1' style='border-collapse: collapse; width: 550px; height: 300px; text-align: center;'>";
        echo "<tr>";
        echo "<th style='background-color: #f2f2f2; color: #333; font-weight: bold;'>ID</th>";
        echo "<th style='background-color: #f2f2f2; color: #333; font-weight: bold;'>Назва Компанії</th>";
        echo "<th style='background-color: #f2f2f2; color: #333; font-weight: bold;'>Кількість працівників</th>";
        echo "<th style='background-color: #f2f2f2; color: #333; font-weight: bold;'>Контракти</th>";
        echo "</tr>";
    
     while($row = $result->fetch_assoc()) {
    echo "<tr>";
    echo "<td>".$row["idFirm"]."</td>";
    echo "<td>".$row["companyName"]."</td>";
    echo "<td>".$row["numberWorkers"]."</td>";
    echo "<td><a href='firm_contracts.php?idFirm=" . $row["idFirm"] . "'>Деталі про контракти " . $row["companyName"] . "</a></td>";
    echo "</tr>";
   }
   echo "</table>";
    } else {
        echo "Фірми відсутні.";
    }
    echo "<h3><a href='main.php'>Повернутись на головну сторінку сайту</a></h3>";
    
 $connection->close();
 ?>