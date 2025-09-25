# Queue management app
## :notebook: Description
The app simulates a series of clients arriving for service, entering queues, waiting, being served and finally leaving the queues. All clients are generated when the 
simulation is started, and are characterized by three parameters: ID, arrival time (simulation time when they are ready to enter the queue) and service time 
(waiting time when the client is in front of the queue, being served). The app tracks the total time spent by every client in the queues and computes the average 
waiting time, as well as the average service time. Each client is added to the queue with the minimum waiting time when its arrival time is greater than or equal 
to the simulation time.<br><br>

## User interaction
The user inserts the input data in the GUI of the application, which includes:
* Number of clients
* Number of queues
* Simulation interval
* Minimum and maximum arrival time
* Minimum and maximum service time

Clients are randomly generated based on this data and displayed to the user in the graphical interface. After pressing the "Start Simulation" button, a new window opens,
displaying the real-time evolution of the queues, updated every second until the simulation ends. This allows the user to observe a possible scenario based on the input 
data. At the end, the application closes automatically and generates a text file named **`log_of_events`**, which contains the queues evolution in text format for later 
review. It also includes statistics such as the average waiting time, the average service time and peak hour for the simulation interval.<br><br>

## Class diagram
<div align="center">
  <img width="1100" src="https://github.com/user-attachments/assets/e582c331-f4d6-4724-9aa5-14329642b867" />
</div><br><br>

The **`Generator`** class is responsible for generating clients and servers (each server represents a queue) based on the input data provided by the user. 

There are two strategies for assigning clients to queues: **Empty Queue** and **Shortest Time**. The strategy is selected **dynamically** during the application’s runtime. Clients are prioritized for empty queues, and if none are available, they are assigned to the queue with the shortest waiting time. The **`Scheduler`** class is responsible for selecting the strategy and adding clients to the appropriate queue.

The **`SimulationClock`** class simulates time passing. It notifies all threads (queues and the simulation manager) each time a second passes, allowing the queues to evolve over time. A ***lock*** is used as a synchronization mechanism.

**`SimulationManager`** is the main class of the app. It uses the **generator** to obtain the clients and servers. After the user confirms the start of the simulation, the manager’s thread begins. It utilizes the **clock** and the **scheduler** to simulate time passing and to add arriving clients to the appropriate queues. **Server** threads are notified, and each queue processes its clients every second. For synchronization purposes, a **`CyclicBarrier`** is used, along with a concurrent collection, **`ConcurrentLinkedQueue`**, which stores waiting and service times for the final statistics.

The **`Client`** and **`Server`** classes form the data model. Each **`Server`** represents a queue responsible for processing clients. Every queue runs as an independent thread. Synchronization mechanisms such as concurrent collections (**`BlockingQueue`**), **`AtomicInteger`** variables, **`volatile`** variables, locks, and a **`CyclicBarrier`** are used. At each second of the simulation, the barrier ensures that all threads complete their actions before moving to the next second. This process repeats until the end of the simulation.<br><br>

## GUI
The graphical user interface is implemented using the Swing API.<br>

<div align="center">
  <img width="720" height="380" alt="image" src="https://github.com/user-attachments/assets/7493365d-6b70-4c8a-937b-9ad4b87fd3a7" />
 <br><br>
  <img width="322" height="141" alt="image" src="https://github.com/user-attachments/assets/4305e622-d935-4951-8898-a40bd5748395" />
  <br><br>
  <img width="415" height="543" alt="image" src="https://github.com/user-attachments/assets/97206e5c-4fbb-4e72-852b-d775322402f4" />
  <br><br>
  <img width="840" height="212" alt="image" src="https://github.com/user-attachments/assets/9dbf5f15-fee2-430f-aa0c-ad061f5a2724" />
  <br><br>
  <img width="430" height="520" alt="image" src="https://github.com/user-attachments/assets/ed798239-947b-4be9-8de7-32a7c184324f" />
  <br><br>
  <img width="321" height="143" alt="image" src="https://github.com/user-attachments/assets/385dd6ae-a06d-444d-a26b-25c03f4563d6" />
</div>

