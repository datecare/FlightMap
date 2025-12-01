# Flight Simulator

This project is a simple 2D GUI Flight Simulator in Java using the AWS framework. Add and display flights.

Features include:

1. Adding Airports and Scheduling Flights through a Dialog

2. Loading and Saving Airports and Flights into a .csv file

3. Starting, Pausing and Restarting the Simulation 

4. Hiding or Selecting Airports via checkbox / clicking

5. Waiting queue for flights flying from the same starting airport close to each other

6. Killer Thread after 1 minutes of user inactivity

---

## Adding Airports and Flights

You can add Airports and Schedule Flights by clicking on Add->Input (Ctrl+A) 

- Adding Airports

<div align="center">
<img src="images/add1.png" alt="Demo" style="border:1px solid #ddd; border-radius:8px;"/>
</div>

- Adding Flights

<div align="center">
<img src="images/add2.png" alt="How flights look on the map" style="border:1px solid #ddd; border-radius:8px;"/>
</div>

- Sidenote: check out csv/simulation.csv for load/store .csv format
---

## Simulation

After adding the Airports and Flights, you can start the simulation by clicking the  **Start Simulation Button**.

10 minutes of simulation time pass every 1 second. Ticks occur every 0.2 seconds.

<div align="center">
<img src="images/flights.png" alt="Demo" style="border:1px solid #ddd; border-radius:8px;"/>
</div>

Flights are represented by blue dots and move with constant velocity towards the target Airport.

---

