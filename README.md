# Queue management app
## Description
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
review. It also includes statistics such as the average waiting time, the average service time and peak hour for the simulation interval.
