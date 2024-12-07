using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class heartratethreshold_yellow : MonoBehaviour
{
    public ParticleSystem particleSystem;
    private int triggerThreshold = 100;
    private bool isParticlePlaying = false;
    private int currentHeartrate = 0;

    private ParticleSystem.MainModule particleMain;
    private Color originalColor;

    private bool pause = false;
    private float timer = 0f;

    // Start is called before the first frame update
    void Start()
    {
        particleMain = particleSystem.main;
        originalColor = particleMain.startColor.color;

        particleMain.startColor = Color.gray;

        particleSystem.Play();
    }

    // Update is called once per frame
    void Update()
    {
        if(!pause){
            timer += Time.deltaTime;

            if (timer >= 2f){
              particleSystem.Pause();
              pause = true;
            }
        }

        currentHeartrate = HeartrateEventManager.heartrate;

        if (currentHeartrate >= triggerThreshold && !isParticlePlaying)
        {
            ActivateParticleSystem();
        }
        else if (currentHeartrate < triggerThreshold && isParticlePlaying)
        {
            DeactivateParticleSystem();
        }
    }

    private void ActivateParticleSystem()
    {
        if (!isParticlePlaying)
        {
            particleMain.startColor = originalColor;

            particleSystem.Play();
            isParticlePlaying = true;
        }
    }

    private void DeactivateParticleSystem()
    {

        if (isParticlePlaying)
        {
            isParticlePlaying = false;

            particleMain.startColor = Color.gray;
            particleSystem.Play();

            float timer1 = 0f;
            timer1 += Time.deltaTime;

            if (timer1 >= 2f){
              particleSystem.Pause();
            }
        }
    }


}
