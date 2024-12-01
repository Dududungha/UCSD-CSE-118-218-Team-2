using System;
using System.Net;
using System.Net.Sockets;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Threading;

public class TCPServer : MonoBehaviour
{
    private TcpListener server;
    private Thread serverThread;
    private const int PORT = 7777;

    // Start is called before the first frame update
    void Start()
    {   
        serverThread = new Thread(new ThreadStart(StartServer));
        serverThread.Start();
    }

    private void StartServer() {
        try {
            server = new TcpListener(IPAddress.Any, PORT);
            server.Start();
            Debug.Log("Server started... Waiting for connections...");

            while (true) {
                try {
                    TcpClient client = server.AcceptTcpClient();
                    Debug.Log("Client Connected.");

                    Thread clientThread = new Thread(new ParameterizedThreadStart(HandleClient));
                    clientThread.Start(client);
                } catch (Exception e) {
                    Debug.Log("Server error: " + e.Message);
                }
            }
        } catch (Exception e) {
            Debug.Log("Server failed to start: " + e.Message);
        }
    }

    private void HandleClient(object obj) {
        TcpClient client = (TcpClient) obj;
        NetworkStream stream = client.GetStream();
        byte[] data = new byte[4];
        int bytesRead;

        while ((bytesRead = stream.Read(data, 0, data.Length)) != 0) {
            // string message = System.Text.Encoding.ASCII.GetString(data, 0, bytesRead);
            // Debug.Log("Received message: " + message);
            // Debug.Log("Bytes read: " + bytesRead);

            if (bytesRead >= 4) {
                // Debug.Log("Bytes: " + data[0] + " " + data[1] + " " + data[2] + " " + data[3]);
                Array.Reverse(data);
                // Convert the first 4 bytes to a float (assuming the data is in little-endian format)
                
                int heartrate = BitConverter.ToInt32(data, 0);
                Debug.Log("Received heartrate: " + heartrate);

                // update static heartrate
                HeartrateEventManager.heartrate = heartrate;
                HeartrateEventManager.HeartrateIsUpdated = true;
            } else {
                Debug.Log("Received heartrate is not a valid float.");
            }
        }

        client.Close();
    }

    void OnApplicationQuit() {
        server.Stop();
        serverThread.Abort();
        Debug.Log("Server Stopped.");
    }
}
