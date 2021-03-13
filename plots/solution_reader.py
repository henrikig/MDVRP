from parameters import SOLUTIONS_FOLDER, SOLUTION_FILE
from collections import namedtuple


Route = namedtuple("Route", ["depot", "customers"])


def read_solution(filename):
    with open(filename, "r") as f:
        solution_description = f.readlines()

    solution_cost = float(solution_description[0])

    routes = []

    for vehicle_line in solution_description[1:]:
        vehicle_line = [entry for entry in vehicle_line.split()]
        depot = int(vehicle_line[0])
        customers = [int(entry) for entry in vehicle_line[5:-1]]
        route = Route(depot=depot, customers=customers)
        routes.append(route)

    return routes, solution_cost


read_solution(SOLUTIONS_FOLDER + SOLUTION_FILE)
