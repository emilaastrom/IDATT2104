#include <iostream>
#include <thread>
#include <vector>
#include <atomic>
#include <mutex>
#include <chrono>

using namespace std;

int sum = 0;
mutex coutMutex;
mutex sumMutex;

/* void printPrimeNumber(int number, int threadId) {
    lock_guard<mutex> lock(coutMutex);
    cout << number << " is a prime number, found by thread: " << threadId << endl;
} */


bool isPrime(int number) {
    if (number <= 1) return false;
    if (number <= 3) {
        lock_guard<mutex> lock(coutMutex);
        cout << number << " is a prime number, found by thread: " << this_thread::get_id() << endl;
        return true;
        }

    if (number % 2 == 0 || number % 3 == 0) return false;

    for (int i = 5; i * i <= number; i = i + 6) {
        if (number % i == 0 || number % (i + 2) == 0) return false;
    }
    lock_guard<mutex> lock(coutMutex);
    cout << number << " is a prime number, found by thread: " << this_thread::get_id() << endl;
    return true;
}

void sumPrimes(int start, int end, int threadId) {
    for (int i = start; i < end; i++) {
        if (isPrime(i)) {
            sumMutex.lock();
            // printPrimeNumber(i, threadId);
            sum += i;
            sumMutex.unlock();
        }
    }
}

int main() { 
    auto startTime = chrono::high_resolution_clock::now();

    vector<thread> threads;

     threads.emplace_back(sumPrimes, 0, 25000, 1);
    threads.emplace_back(sumPrimes, 25001, 50000, 2);
    threads.emplace_back(sumPrimes, 50001, 75000, 3);
    threads.emplace_back(sumPrimes, 75001, 100000, 4);

    /* threads.push_back(thread(sumPrimes, 0, 25000, 1));
    threads.push_back(thread(sumPrimes, 25001, 50000, 2));
    threads.push_back(thread(sumPrimes, 50001, 75000, 3));
    threads.push_back(thread(sumPrimes, 75001, 100000, 4)); */

/*     threads[0].join();
    threads[1].join();
    threads[2].join();
    threads[3].join(); */
    for (auto& t : threads) t.join();
    
    auto endTime = chrono::high_resolution_clock::now();

    auto duration = chrono::duration_cast<chrono::milliseconds>(endTime - startTime);

    cout << "\nTime spent: " << duration.count() << " milliseconds" << endl;

    cout << "\nSum of prime numbers: " << sum << endl;

}