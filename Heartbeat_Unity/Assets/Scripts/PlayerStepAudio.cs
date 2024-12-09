using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerStepAudio : MonoBehaviour
{
    public AudioSource StepAudio;

    // Update is called once per frame
    void Update()
    {
        if (Input.GetAxis("Horizontal") != 0 || Input.GetAxis("Vertical") != 0) {
            StepAudio.Play();
        }
    }
}
