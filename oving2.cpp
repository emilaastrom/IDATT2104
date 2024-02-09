#include <iostream>
#include <thread>
#include <vector>
#include <atomic>
#include <mutex>
#include <chrono>
#include <list>
#include <unistd.h>

using namespace std;

class Workers {
private:
    int numThreads;
    vector<thread> threads;
    condition_variable cv;
    list <function <void()> > tasks;
    mutex tasks_mutex;
    atomic<bool> running_threads;

public:
    Workers(int numThreads) {
        // Assign number of threads
        this->numThreads = numThreads;
    }

    void start() {
        // Start the threads
        cout << "- Starting " << numThreads << " threads" << endl;
        running_threads.exchange(true);
        for (int i = 0; i < numThreads; i++) {
            threads.push_back(thread([this] {
                while (running_threads) {
                    function<void()> task;
                    {
                        unique_lock<mutex> lock(tasks_mutex);
                        cv.wait(lock, [this] { return !tasks.empty() || !running_threads; });
                        if (!running_threads) return;
                        task = move(tasks.front());
                        tasks.pop_front();
                    }
                    task();
                }
            }));
        }
    }

    void stop() {
        // Stop the threads
        running_threads.exchange(false);
        cv.notify_all();
    }

    void post(const function<void()>& func) {
        // Add a task to the list
        {
            lock_guard<mutex> lock(tasks_mutex);
            tasks.push_back(func);
        }
        cv.notify_one();
    }

    void post_timeout(const function<void()>& func) {
        // Add a task to the list with a delay of 1s
        this_thread::sleep_for(chrono::seconds(1));
        {
            lock_guard<mutex> lock(tasks_mutex);
            tasks.push_back(func);
        }
        cv.notify_one();
    }

    void join() {
        // Join the threads when the task list is empty
        cout << "- Join call. Checking to see if the task list is empty" << endl;
        while(!tasks.empty()){
            this_thread::sleep_for(chrono::milliseconds(100));
        }
        running_threads.exchange(false);
        cv.notify_all();
        for (auto& t : threads) {
            t.join();
        }
        cout << "- Task list empty, threads have been joined" << endl;
    }
};


int main()
{
    Workers worker_threads(4); 
    Workers event_loop(1);

    worker_threads.start();
    event_loop.start();

    worker_threads.post([] {
    // Task A
    cout << "Task A - by Workers" << endl;
    }); 

    worker_threads.post([] {
    // Task B
    cout << "Task B - by Workers" << endl;
    // Might run in parallel
    });

    event_loop.post([] {
    // Task C
    cout << "Task C - by Event Loop" << endl;
    // Might run in parallel
    }); 

    event_loop.post([] {
    // Task D
    cout << "Task D - by Event Loop" << endl;
    // Will run after task C // Might run in parallel
    });

    worker_threads.post_timeout([] {
    // Extra task 
    cout << "Task F (EXTRA) - by Workers with 1s timeout" << endl;
    });

    worker_threads.join(); // Calls join()
    event_loop.join(); // Calls join()
    return 0;
}
