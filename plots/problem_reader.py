from parameters import PROBLEM_FOLDER, PROBLEM_FILE
from collections import namedtuple

Customer = namedtuple("Customer", ["x", "y"])
Depot = namedtuple("Depot", ["x", "y"])


def read_problem(filename):

    with open(filename, "r") as f:
        problem_description = f.readlines()

    problem_info = problem_description[0].split()

    num_customers = int(problem_info[1])
    num_depots = int(problem_info[2])

    customers = {}
    depots = {}

    for customer_line in problem_description[1 + num_depots:-num_depots]:
        customer_line = [int(entry) for entry in customer_line.split()]
        customers[customer_line[0]] = Customer(x=customer_line[1], y=customer_line[2])

    for depot_line in problem_description[1 + num_depots + num_customers:]:
        depot_line = [int(entry) for entry in depot_line.split()]
        depots[depot_line[0] - num_customers] = Depot(x=depot_line[1], y=depot_line[2])

    return customers, depots


read_problem(PROBLEM_FOLDER + PROBLEM_FILE)
