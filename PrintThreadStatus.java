public class PrintThreadStatus {
	public synchronized void	printThreadStatus(long threadId, int numberOfFile) {
		System.out.println("Thread-" + threadId + " start download file number " +
				numberOfFile);
	}
}
