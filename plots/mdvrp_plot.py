import numpy as np
import matplotlib.pyplot as plt
import sys
import random
from problem_reader import read_problem
from solution_reader import read_solution
from parameters import PROBLEM_FOLDER, SOLUTIONS_FOLDER


def plot(prob="p01"):
    customers, depots = read_problem(PROBLEM_FOLDER + prob)

    routes, cost = read_solution(SOLUTIONS_FOLDER + prob + ".res")

    customer_x = [c.x for _, c in customers.items()]
    customer_y = [c.y for _, c in customers.items()]

    depot_x = [d.x for _, d in depots.items()]
    depot_y = [d.y for _, d in depots.items()]

    plt.scatter(customer_x, customer_y)
    plt.scatter(depot_x, depot_y, c="r", marker="s")

    for route in routes:
        depot = route.depot

        color = random.choice(["#e57373", "#f06292", "#ba68c8", "#b39ddb", "#9fa8da",
                               "#90caf9", "#80cbc4", "#a5d6a7", "#fff176", "#ffb74d"])

        plt.plot([depots.get(depot).x, customers.get(route.customers[0]).x],
                 [depots.get(depot).y, customers.get(route.customers[0]).y],
                 c=color, alpha=1)

        for i in range(len(route.customers) - 1):
            customer1 = customers.get(route.customers[i])
            customer2 = customers.get(route.customers[i + 1])

            plt.plot([customer1.x, customer2.x],
                     [customer1.y, customer2.y],
                     c=color, alpha=1)

        plt.plot([customers.get(route.customers[-1]).x, depots.get(depot).x],
                 [customers.get(route.customers[-1]).y, depots.get(depot).y],
                 c=color, alpha=1)

    plt.title(prob + ": " + str(cost))
    plt.show()


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Please specify problem in arguments.")
        print("Plotting default problem p01.")
        problem = "p01"
    else:
        problem = sys.argv[1]

    plot(problem)
