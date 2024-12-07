using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class heartratethreshold : MonoBehaviour
{
    public ParticleSystem particleSystem;
    private int triggerThreshold = 0;
    private bool isParticlePlaying = false;
    private int currentHeartrate = 0;

    private ParticleSystem.MainModule particleMain;
    private Color originalColor;

    private bool pause = false;
    private float timer = 0f;

    // Start is called before the first frame update
    void Start()
    {
      // Cache the main module of the particle system for efficiency
        // paticleSystem = GetComponent<ParticleSystem>();

        particleMain = particleSystem.main;
        originalColor = particleMain.startColor.color;

        Debug.Log("Color at start? "+originalColor);

        // particleSystem.Pause();

        particleMain.startColor = Color.gray;

        particleSystem.Play();

        // particleSystem.Pause();
        Debug.Log("playing at start? "+particleSystem.isPlaying);

        Debug.Log("Color gray after? "+particleMain.startColor.color);

        Debug.Log($"Threshold for {gameObject.name}: {triggerThreshold}");
    }

    // Update is called once per frame
    void Update()
    {
        // Debug.Log("My color "+particleMain.startColor.color);
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
            Debug.Log($"heartrate >= threshold for {gameObject.name}!");
            ActivateParticleSystem();
        }
        else if (currentHeartrate < triggerThreshold && isParticlePlaying)
        {
            Debug.Log("heartrate < threshold");
            DeactivateParticleSystem();
        }

    }

    private void ActivateParticleSystem()
    {
        Debug.Log("threshold when activate: "+currentHeartrate);
        Debug.Log("playing? "+particleSystem.isPlaying);
        if (!isParticlePlaying)
        {
            particleMain.startColor = originalColor;

            particleSystem.Play();
            isParticlePlaying = true;
            Debug.Log($"Particle System Activated for {gameObject.name}!");
        }
    }

    private void DeactivateParticleSystem()
    {
        Debug.Log("threshold when deactivate: "+currentHeartrate);

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
            Debug.Log($"Particle System Deactivated for {gameObject.name}!");
        }
    }


}
